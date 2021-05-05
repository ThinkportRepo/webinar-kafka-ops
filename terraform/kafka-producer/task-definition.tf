resource "aws_ecs_cluster" "producer-ecs-cluster" {
  name = "webinar-kafka-ops-producer"
  tags = local.tags
}

resource "aws_ecs_service" "producer-service" {
  name            = "webinar-kafka-ops-consumer"
  cluster         = aws_ecs_cluster.producer-ecs-cluster.id
  task_definition = aws_ecs_task_definition.producer-task-definition.arn
  launch_type     = "FARGATE"
  tags            = local.tags
  network_configuration {
    subnets = [
      aws_subnet.subnet_az1.id,
      aws_subnet.subnet_az2.id,
      aws_subnet.subnet_az3.id
    ]
    assign_public_ip = true
  }
  desired_count = 1
}

resource "aws_ecs_task_definition" "producer-task-definition" {
  family                   = "webinar-kafka-ops"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  memory                   = "1024"
  cpu                      = "512"
  execution_role_arn       = "arn:aws:iam::562760952310:role/ecsTaskExecutionRole"
  tags                     = local.tags
  container_definitions = jsonencode(
    [
      {
        name : "webinar-kafka-ops-producer",
        image : "562760952310.dkr.ecr.eu-central-1.amazonaws.com/webinar-kafka-ops-producer:latest",
        memory : 1024,
        cpu : 512,
        essential : true,
        entryPoint : ["/"],
        portMappings : [
          {
            containerPort : 8080,
            hostPort : 8080
          }
        ],
        environment : [
          { name : "kafka_bootstrap_servers", value : var.kafka_connection_string }
        ],
      }
    ]
  )
}