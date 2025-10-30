FROM grafana/promtail:latest

RUN mkdir -p /etc/promtail

COPY monitoring/promtail.yml /etc/promtail/config.yml