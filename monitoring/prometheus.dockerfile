FROM prom/prometheus:latest
# Faylning to'g'ri nomini ko'rsatamiz
COPY monitoring/prometheus.yml /etc/prometheus/prometheus.yml