# Implementation Milestones and roadmap

## Milestone 1: Project preparation (3 weeks)

### Understanding the product (2-3 days)

#### Objectives
- Team to understand product business model and stakeholders
- setup and share documentation workspace (confluence or similar)
- Read through available documentation, presentations, prototype or mockups
- Warm up team before product architecture discussion

#### Key results

- All team members has access to documentation workspace and read them
- Discussion / brainstorming session is organized to list out key technological challenges product architecture should address
- First meeting notes are saved documentation workspace

### Architectural design and technology stack discussion (1 week)

#### Objectives
- Get team's buy in on implementation methodologies, frameworks and overall stack
- Identify architectural components and draft some "hello world" code to start with
- Make sure everyone is familiar with tech stack and let them fill the gaps (if any)

#### Key results
- Minimal code is written for some components
- Components are ready for CI/CD pipeline
- High level of architecture is documented

### Dev environment and CI/CD setup (1 week)

#### Objectives
- Define code branching model
- Configure CI/CD pipelines for dev environment for as much components/services as possible

#### Key results
- Minimal code is written for all identified components
- At least one simple test of a kind is written per component (unit, integration, etc..)
- Code merging triggers CI/CD and code deploys on dev servers if tests pass


## Milestone 2: Landing page with some services (3 weeks)
Ideally scope and/or timings should be defined by team members

### Define service API initial contracts, and make UI prototypes of landing page

#### Objectives
- Frontend: identify ui elements and setup initial components
- Backend: Define some initial contract for 
    - identity service -  register, login and logout
    - recommendations - top thoughts, most commented thoughts
    - books - get single thought (quote of book)
- Backend: Prepare mock services for all
- Backend: Prepare service stubs
- Backend: Prepare service API tests

#### Key results

- Mocks and stubs of services are available and frontend team should be able to make calls from web
- Tests on stubs yield 100% code coverage
- Frontend developers are able to run backend service locally to make calls

### Implementation of identity service

#### Objectives
- Write first code and measure development speed
- Enrich tests
- get register, log in and out cycle really working
- motivate team by show first crop

#### Key results
- on development environment one can 
    - register a user
    - log in and see user logged in
    - log out 
- get at least 80% code coverage on real implementation of service

## Milestone 3: Landing page full scope with dummy recommendation engine (3 weeks)

### Initial implementation of books service
#### Out of scope
- books internal api for administration service
- books internal api for payment service 
#### Objectives
- initial implementation of books service
    - book and thoughts CRUD
    - set book for sale

#### Key results
- book CRUD operations are in place
- 80% code coverage on implementation

### Implementation of recommendation API service
#### Objectives
- fully implement recommendation API (both internal and external)
- stub implementation of recommendation engine (randomly choose books and provision API service with data)
- get full cycle in place
    - user adds book
    - books service sends update to message queue
    - recommendation engine stores the book info locally and generates random recommendation lists
    - recommendation engine publishes lists to recommendation API service through internal API
    - recommendation API serves provisioned lists through external API
#### Key results
- full cycle works
- 80 % coverage rate is kept
- landing page UI finished and every update on books service modifies the recommendation lists