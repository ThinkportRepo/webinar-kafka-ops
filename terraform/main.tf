module "msk" {
  source = "./msk"
}

module "producer" {
  source  = "./kafka-producer"
  servers = 1
}

module "consumer" {
  source  = "./kafka-consumer"
  servers = 1
}