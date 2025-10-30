FROM prom/prometheus:latest

COPY monitoring/prom.yml /etc/prometheus/prometheus.yml