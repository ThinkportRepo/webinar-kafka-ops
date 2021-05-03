module "kafka" {
  source = "../msk"
  subnet = [module.kafka.kafka_subnet]
}