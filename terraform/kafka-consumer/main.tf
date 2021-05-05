provider "aws" {
  profile = "dev-thinkport"
  region  = "eu-central-1"
}

variable "kafka_connection_string" {
  type = string
  default = "b-3.webinar-kafka-ops.sg6u26.c3.kafka.eu-central-1.amazonaws.com:9094,b-2.webinar-kafka-ops.sg6u26.c3.kafka.eu-central-1.amazonaws.com:9094,b-1.webinar-kafka-ops.sg6u26.c3.kafka.eu-central-1.amazonaws.com:9094"
}

locals {
  tags = {
    project     = "webinar-kafka-ops"
    responsible = "Laszlo Csoti"
    ttl         = "2021-05-30"
  }
}

variable "subnet_az_1" {
  type = string
  default = "subnet-0dad57b9ee2898291"
}

variable "subnet_az_2" {
  type = string
  default = "subnet-0c11185e624d0318c"
}

variable "subnet_az_3" {
  type = string
  default = "subnet-071b92b9400843087"
}

variable "private_vpc_id" {
  type        = string
  default     = "vpc-09b84ce984e473d67"
  description = "Existing VPC"
}

resource "aws_cloudwatch_log_group" "producer_logs" {
  name = "webinar-kafka-ops"
  tags = local.tags
}