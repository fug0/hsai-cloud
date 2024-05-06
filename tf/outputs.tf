output "servers" {
  value = {
    event_reminder_server = openstack_compute_instance_v2.event_reminder_server.access_ip_v4
    event_reminder_db_server = openstack_compute_instance_v2.event_reminder_db_server.access_ip_v4
  }
}