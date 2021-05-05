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

data "aws_availability_zone" "az1" {
  name = "eu-central-1a"
}

data "aws_availability_zone" "az2" {
  name = "eu-central-1b"
}

data "aws_availability_zone" "az3" {
  name = "eu-central-1c"
}

resource "aws_subnet" "subnet_az1" {
  availability_zone = data.aws_availability_zone.az1.name
  cidr_block        = "10.0.0.16/28"
  vpc_id            = var.private_vpc_id
  tags = merge(local.tags, {
    Name = "az1_private"
  })
}

resource "aws_subnet" "subnet_az2" {
  availability_zone = data.aws_availability_zone.az2.name
  cidr_block        = "10.0.0.32/28"
  vpc_id            = var.private_vpc_id
  tags = merge(local.tags, {
    Name = "az2_private"
  })
}

resource "aws_subnet" "subnet_az3" {
  availability_zone = data.aws_availability_zone.az3.name
  cidr_block        = "10.0.0.48/28"
  vpc_id            = var.private_vpc_id
  tags = merge(local.tags, {
    Name = "az3_private"
  })
}

variable "private_vpc_id" {
  type        = string
  default     = "vpc-09b84ce984e473d67"
  description = "Existing VPC"
}