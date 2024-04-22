#!/usr/bin/env python3

import argparse
import yaml
from openstack import connection

def create_or_update_stack(conn, stack_name, template, env):
    stack = conn.orchestration.find_stack(stack_name)

    if stack:
        print(f"Updating stack: {stack_name}")
        conn.orchestration.update_stack(
            stack.id,
	    template=template,
            environment=env
        )
    else:
        print(f"Creating stack: {stack_name}")
        conn.orchestration.create_stack(
            name=stack_name,
            template=template,
            environment=env
        )

parser = argparse.ArgumentParser(description="Create or update an OpenStack Heat stack.")
parser.add_argument("stack_name", help="Name of the Heat stack")
parser.add_argument("template_path", help="Path to the Heat template file")
parser.add_argument("environment_path", help="Path to the environment file")

args = parser.parse_args()

auth = {
    "auth_url": "https://cloud.crplab.ru:5000",
    "project_id": "a02aed7892fa45d0bc2bef3b8a08a6e9",
    "username": "master2022",
    "password": "J8F3LGa*7KU7ye",
    "user_domain_name": "Default",
    "compute_api_version": "3",
    "identity_interface": "public"
}

conn = connection.Connection(**auth)

stack_name = args.stack_name

with open(args.template_path, 'r') as template_file:
    template = template_file.read()

with open(args.environment_path, 'r') as env_file:
    env = env_file.read()

create_or_update_stack(conn, stack_name, template, env)
