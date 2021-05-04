variable "private_vpc_id" {
  type = string
  default = "vpc-09b84ce984e473d67"
  description = "Existing VPC"
}

variable "msk_key_arn" {
  type = string
  default ="arn:aws:kms:eu-central-1:562760952310:key/93849fac-b44f-46c1-8b08-75bb94ff25ef"
  description = "Existing key"
}