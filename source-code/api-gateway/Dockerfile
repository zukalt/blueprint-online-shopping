FROM nginx

ENV RESOLVER_ADDR=10.0.0.2

COPY nginx.conf.template /etc/nginx/nginx.conf.template

ADD wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

ENTRYPOINT [ "/wait-for-it.sh","-t", "0", "swagger-ui.local:8080", "--" ]

CMD /bin/bash -c "envsubst '\$RESOLVER_ADDR' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf && exec nginx -g 'daemon off;'"
