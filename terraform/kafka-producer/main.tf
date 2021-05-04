provider "aws" {
  profile = "dev-thinkport"
  region  = "eu-central-1"
}

module "kafka" {
  source = "../msk"
}

locals {
  tags = {
    project     = "webinar-kafka-ops"
    responsible = "Laszlo Csoti"
    ttl         = "2021-05-30"
  }
}