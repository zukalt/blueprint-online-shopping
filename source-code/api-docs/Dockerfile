FROM swaggerapi/swagger-ui

ENV URLS [ { url: "./docs/auth-api.yml", name: "Auth" }, { url: "./docs/recommendations-api.yml", name: "Recommendations" },{ url: "./docs/books-api.yml", name: "Books" }]
COPY *.yml /usr/share/nginx/html/docs/
