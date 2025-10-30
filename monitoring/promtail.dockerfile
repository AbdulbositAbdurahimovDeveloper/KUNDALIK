FROM grafana/promtail:latest
RUN mkdir -p /etc/promtail
# Faylning to'g'ri nomini ko'rsatamiz
COPY monitoring/config.yml /etc/promtail/config.yml