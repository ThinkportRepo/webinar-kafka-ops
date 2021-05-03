resource "aws_ecs_cluster" "consumer-ecs-cluster" {
  name = "ecs-consumer-cluster"
}

resource "aws_ecs_service" "demo-ecs-service-two" {
  name            = "webinar-kafka-ops-consumer"
  cluster         = aws_ecs_cluster.consumer-ecs-cluster.id
  task_definition = aws_ecs_task_definition.consumer-task-definition.arn
  launch_type     = "FARGATE"
  network_configuration {
    subnets          = [
      module.kafka.kafka_subnet
    ]
    assign_public_ip = true
  }
  desired_count = 1
}

resource "aws_ecs_task_definition" "consumer-task-definition" {
  family                   = "ecs-task-definition-demo"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  memory                   = "1024"
  cpu                      = "512"
  execution_role_arn       = "arn:aws:iam::562760952310:role/ecsTaskExecutionRole"
  container_definitions    = json_encode(
  [
    {
      name: webinar-kafka-ops-producer,
      image: "562760952310.dkr.ecr.eu-central-1.amazonaws.com/webinar-kafka-ops-consumer:latest",
      memory: 1024,
      cpu: 512,
      essential: true,
      entryPoint: ["/"],
      portMappings: [
        {
          containerPort: 8080,
          hostPort: 8080
        }
      ],
      environment: [
        {name: kafka_bootstrap_servers, value: module.kafka.bootstrap_brokers_tls}
      ],
    }
  ]
  )
}