## Landing page description

Landing page consists of header, 3 (or 2 if not logged in) content sections and footer.

#### Heading 
Contains logo, title and either logged in user info or log in button
#### Content area 

Contains following sections
- Recommended Thoughts (visible to logged in users)
- Random Thoughts (visible to all)
- Most discussed Thoughts (visible to all)

All sections retrieve data from [Recommendations service](../architecture/services/recommendations/index.md) through it's Web API. 
Section should render `more...` button to load next page of data. 
If no more pages are available or section itself contains no data section should render appropriate message.

Each section contains 5 quote boxes. Clicking on the box user navigates to [Single Thought View](./single-thought-view.md)

#### Footer
Contains links to `about project`, `contacts` and `FAQ` pages.

