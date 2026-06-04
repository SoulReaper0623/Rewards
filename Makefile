.PHONY: run stop logs rebuild test

run:
	docker compose up --build

stop:
	docker compose down

logs:
	docker compose logs -f app

rebuild:
	docker compose down
	docker compose up --build

test:
	mvn test
