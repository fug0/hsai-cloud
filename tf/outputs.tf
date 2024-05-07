output "servers" {
  value = {
    docker_server = openstack_compute_instance_v2.event_reminder_docker_server.access_ip_v4
  }
}