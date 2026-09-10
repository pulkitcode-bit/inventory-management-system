# Inventory Management System — Design & Architecture (Week 1)

Junior Java Developer Internship (YuvaIntern — NSDC Job Simulation)
**Author:** Pulkit ([pulkitcode-bit](https://github.com/pulkitcode-bit))

## Week 1 Task: Project Planning and Architecture Design

This repository contains the Week 1 deliverable: a conceptual architecture and
design plan for a Java-based Inventory Management System (IMS) for a small
retail business, built on Java + Spring Boot + MySQL.

## Contents

| File | Description |
|---|---|
| `docs/Week1_Inventory_Design_Document.docx` | Full design document — requirements, architecture, design patterns, DB schema, code scaffolding |
| `docs/Week1_Inventory_Design_Document.pdf` | PDF version of the same document |
| `docs/architecture_diagram.png` | High-level layered architecture diagram |

## Summary

- **Modules:** Product Management, Stock Control, Order Processing, Reporting, User/Auth
- **Architecture:** Layered (Controller → Service → Repository → MySQL)
- **Design Patterns used:** Layered Architecture, Repository, DTO, Singleton, Observer, Factory, Strategy
- **Stack:** Java 17, Spring Boot, Spring Data JPA, Spring Security, MySQL 8, Maven

See the full document in `docs/` for requirement analysis, rationale behind
each design pattern, the database schema, and code scaffolding/pseudocode for
the core service classes.

## Roadmap

- [x] Week 1 — Project Planning & Architecture Design
- [ ] Week 2 — Core Functionality Implementation
- [ ] Week 3 — Unit Testing and Debugging
- [ ] Week 4 — Code Refactoring and Optimization
- [ ] Week 5 — Integration, Deployment, and Documentation
