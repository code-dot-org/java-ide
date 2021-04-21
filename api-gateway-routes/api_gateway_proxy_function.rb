require 'json'
require 'aws-sdk-sqs'
require 'aws-sdk-lambda'
require 'uri'

def lambda_handler(event:, context:)
  routeKey = event["requestContext"]["routeKey"]
  if routeKey == "$connect"
    return on_connect(event, context)
  elsif routeKey == "$disconnect"
    return on_disconnect(event, context)
  else
    return on_default(event, context)
  end
end

def on_connect(event, context)
  region = get_region(context)

  request_context = event["requestContext"]

  # -- Create SQS for this session --
  sqs_client = Aws::SQS::Client.new(region: region)
  sqs_queue = sqs_client.create_queue(
    queue_name: get_session_id(event) + '.fifo',
    attributes: {"FifoQueue" => "true"}
  )

  # -- Create Lambda for this session --
  lambda_client = Aws::Lambda::Client.new(region: region)
  api_endpoint = "https://#{request_context['domainName']}/#{request_context['stage']}"
  payload = {
    :queueUrl => sqs_queue.queue_url,
    :apiEndpoint => api_endpoint,
    :connectionId => request_context["connectionId"],
    :projectUrl => request_context["authorizer"]["project_url"]
  }
  response = lambda_client.invoke({
    function_name: 'javaBuilderExecuteCode:4',
    invocation_type: 'Event',
    payload: JSON.generate(payload)
  })

  { statusCode: response['status_code'], body: "done" }
end

def on_disconnect(event, context)
  sqs = Aws::SQS::Client.new(region: get_region(context))
  sqs.delete_queue(queue_url: get_sqs_url(event, context))

  { statusCode: 200, body: "success"}
end

def on_default(event, context)
  sqs = Aws::SQS::Client.new(region: get_region(context))
  message = event["body"]
  sqs.send_message(
    queue_url: get_sqs_url(event, context),
    message_body: message,
    message_deduplication_id: SecureRandom.uuid.to_str.gsub("-", ""),
    message_group_id: get_session_id(event),
  )

  { statusCode: 200, body: "success"}
end

# ARN is of the format arn:aws:lambda:{region}:{account_id}:function:{lambda_name}
def get_region(context)
  context.invoked_function_arn.split(':')[3]
end

# SQS queues can only be named with the following characters:
# alphanumeric characters, hyphens (-), and underscores (_)
# See https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/SQS/Client.html#create_queue-instance_method
# The connection ID always ends with an '='. We remove that here so we can use the connection ID as
# our session ID.
def get_session_id(event)
  event["requestContext"]["connectionId"].delete_suffix("=")
end

def get_sqs_url(event, context)
  region = get_region(context)
  # ARN is of the format arn:aws:lambda:{region}:{account_id}:function:{lambda_name}
  account_id = context.invoked_function_arn.split(':')[4]
  connection_id = get_session_id(event)
  "https://sqs.#{region}.amazonaws.com/#{account_id}/#{connection_id}.fifo"
end
