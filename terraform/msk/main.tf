provider "aws" {
  profile = "nvoigt"
  region  = "eu-central-1"
}

locals {
  tags = {
    project = "webinar-kafka-ops"
    responsible = "Laszlo Csoti"
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

resource "aws_security_group" "webinar_msk" {
  name = "webinar-msk"
  tags = local.tags
  vpc_id = var.private_vpc_id
}

resource "aws_kms_key" "kms" {
  description = "example"
  tags = local.tags
}

resource "aws_cloudwatch_log_group" "msk_broker_logs" {
  name = "msk_broker_logs"
  tags = local.tags
}

resource "aws_s3_bucket" "msk_broker_logs" {
  bucket = "msk-broker-logs-bucket-73h3tz3z2g"
  acl    = "private"
  tags = local.tags
}

resource "aws_iam_role" "msk_broker_logs" {
  name = "firehose_msk_broker_logs_role"
  tags = local.tags

  assume_role_policy = <<EOF
{
"Version": "2012-10-17",
"Statement": [
  {
    "Action": "sts:AssumeRole",
    "Principal": {
      "Service": "firehose.amazonaws.com"
    },
    "Effect": "Allow",
    "Sid": ""
  }
  ]
}
EOF
}

resource "aws_kinesis_firehose_delivery_stream" "msk_broker_logs" {
  name        = "terraform-kinesis-firehose-msk-broker-logs-stream"
  destination = "s3"

  s3_configuration {
    role_arn   = aws_iam_role.msk_broker_logs.arn
    bucket_arn = aws_s3_bucket.msk_broker_logs.arn
  }

  tags = merge(local.tags, {
    LogDeliveryEnabled = "placeholder"
  })

  lifecycle {
    ignore_changes = [
      tags["LogDeliveryEnabled"],
    ]
  }
}

resource aws_msk_configuration "auto_create_topics" {
  kafka_versions = ["2.7.0"]
  name = "auto_create_topics"
  server_properties = <<PROPERTIES
    auto.create.topics.enable=true
    default.replication.factor=3
    min.insync.replicas=2
    num.io.threads=8
    num.network.threads=5
    num.partitions=1
    num.replica.fetchers=2
    replica.lag.time.max.ms=30000
    socket.receive.buffer.bytes=102400
    socket.request.max.bytes=104857600
    socket.send.buffer.bytes=102400
    unclean.leader.election.enable=true
    zookeeper.session.timeout.ms=18000
  PROPERTIES
}

resource "aws_msk_cluster" "webinar-kafka-ops" {
  cluster_name           = "webinar-kafka-ops"
  kafka_version          = "2.7.0"
  number_of_broker_nodes = 3

  broker_node_group_info {
    instance_type   = "kafka.t3.small"  #$0.0526 / hour
    ebs_volume_size = 1 #$0.119 / GB
    client_subnets = [
      aws_subnet.subnet_az1.id,
      aws_subnet.subnet_az2.id,
      aws_subnet.subnet_az3.id
    ]
    security_groups = [aws_security_group.webinar_msk.id]
  }

  configuration_info {
    arn = aws_msk_configuration.auto_create_topics.arn
    revision = 0
  }

  encryption_info {
    encryption_at_rest_kms_key_arn = aws_kms_key.kms.arn
  }

  open_monitoring {
    prometheus {
      jmx_exporter {
        enabled_in_broker = true
      }
      node_exporter {
        enabled_in_broker = true
      }
    }
  }

  logging_info {
    broker_logs {
      cloudwatch_logs {
        enabled   = true
        log_group = aws_cloudwatch_log_group.msk_broker_logs.name
      }
      firehose {
        enabled         = true
        delivery_stream = aws_kinesis_firehose_delivery_stream.msk_broker_logs.name
      }
      s3 {
        enabled = true
        bucket  = aws_s3_bucket.msk_broker_logs.id
        prefix  = "logs/msk-"
      }
    }
  }

  tags = local.tags
}

output "zookeeper_connect_string" {
  value = aws_msk_cluster.webinar-kafka-ops.zookeeper_connect_string
}

output "bootstrap_brokers_tls" {
  description = "TLS connection host:port pairs"
  value       = aws_msk_cluster.webinar-kafka-ops.bootstrap_brokers_tls
}

output "kafka_subnet" {
  description = "The subnet where kafka will be set up"
  value       = private_vpc_id
}