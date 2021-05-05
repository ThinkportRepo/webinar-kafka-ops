resource "aws_ecs_cluster" "producer-ecs-cluster" {
  name = "webinar-kafka-ops-producer"
  tags = local.tags
}

resource "aws_ecs_service" "producer-service" {
  name            = "webinar-kafka-ops-producer"
  cluster         = aws_ecs_cluster.producer-ecs-cluster.id
  task_definition = aws_ecs_task_definition.producer-task-definition.arn
  launch_type     = "FARGATE"
  tags            = local.tags
  network_configuration {
    subnets = [
      var.subnet_az_1,
      var.subnet_az_2,
      var.subnet_az_3
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
        logConfiguration: {
          logDriver: "awslogs",
          options: {
            awslogs-group: "webinar-kafka-ops",
            awslogs-region: "eu-central-1",
            awslogs-stream-prefix: "producer"
          }
        },
        essential : true,
        entryPoint : ["java", "-jar", "producer-0.0.1-SNAPSHOT.jar"],
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