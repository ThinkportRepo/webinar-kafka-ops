module "msk" {
  source  = "./msk"
}

module "producer" {
  source  = "./kafka-producer"
}

module "consumer" {
  source  = "./kafka-consumer"
}