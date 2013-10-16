# Copyright (C) 2013  Carl Pulley
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.

include_recipe "java"

remote_directory "#{node[:cloud][:deploy_dir]}/#{node[:cloud][:service]}" do
  source "cloud-deploy"
  purge true
  owner 'root'
  group 'root'
  mode 0755
  files_mode 0644
end

bash "create-service-log" do
  code <<-EOF
  touch /var/log/#{node[:cloud][:service]}.log
  chown #{node[:cloud][:uid]}:adm /var/log/#{node[:cloud][:service]}.log
  EOF
end

service node[:cloud][:service] do
  provider Chef::Provider::Service::Upstart
  supports :restart => true, :start => true, :stop => true
end

template "/etc/init/#{node[:cloud][:service]}.conf" do
  source "cloud.conf.erb"
  owner 'root'
  group 'root'
  mode 0644
end

service node[:cloud][:service] do
  action [:enable, :start]
end
