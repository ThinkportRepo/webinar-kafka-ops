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

variable "subnet_az_1" {
  type    = string
  default = "subnet-0dad57b9ee2898291"
}

variable "subnet_az_2" {
  type    = string
  default = "subnet-0c11185e624d0318c"
}

variable "subnet_az_3" {
  type    = string
  default = "subnet-071b92b9400843087"
}

variable "public_subnet_id" {
  type = string
  default = "subnet-02877c30e1a71283d"
}

variable "private_vpc_id" {
  type        = string
  default     = "vpc-09b84ce984e473d67"
  description = "Existing VPC"
}
variable "public_vpc_id" {
  type        = string
  default     = "vpc-0765c029f6ebba31e"
  description = "Existing VPC"
}

variable "public_internet_gateway_id" {
  type = string
  default = "igw-0275137f34bdc9398"
}