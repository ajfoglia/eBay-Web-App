**********************************************************
TEAM: Me_Only
**********************************************************

Andy Foglia
903895596

****************************************************************
ACADEMIC HONESTY:

I used Twitter bootstrap 2.3.2 (previous experience) to style my website. I know it is the outdated version, but I preferred working with the version I was most comfortable with.

I followed links on W3schools and StackOverflow when solving basic web programming issues like CSS stylesheets and HttpSession.
****************************************************************

1. 4->5

I encrypt the credit card information from 4 to 5 in the figure. I make the credit card number in a form and have the rest of the form inherit values from the item page. When the user hits "Submit", the form is submitted to the server to process the request but I use SSL and port 8443 to ensure the credit card number cannot be eavesdropped on. To ensure unnecessary computation costs, I provide some redirect options for the user to go back to HTTP.

2. I use POST and not GET. This hides the form values in the URL. I also set the price in the session as an attribute so it cannot be modified, or else the session will invalidate. To be even more safe, when I display the price I use the disabled attribute in Bootstrap so the user cannot modify the value on the front-end side either.