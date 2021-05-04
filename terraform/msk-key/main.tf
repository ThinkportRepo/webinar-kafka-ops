provider "aws" {
  profile = "dev-thinkport"
  region  = "eu-central-1"
}

locals {
  tags = {
    project     = "webinar-kafka-ops"
    responsible = "Laszlo Csoti"
    ttl         = "2021-05-30"
  }
}

resource "aws_kms_key" "kms" {
  description = "example"
  tags = local.tags
}

output "aws_msk_key_arn" {
  value = aws_kms_key.kms.arn
}