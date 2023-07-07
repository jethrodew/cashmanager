# Cash Manager
Example Java CLI application serving as a cash manager in a vending machine

Java 17
Maven 3.9.3

Uses Maven build manager and compiles to single runnable jar (target/cashmanager-jar-with-dependencies.jar)

Build with: mvn clean install

Run with: java --jar target/cashmanager-jar-with-dependencies.jar gbp [denominationCounts [runForUserInput]]

When providing denominationCounts it should be formatted like the following: 
200:5,100:10,50:10
e.g.
5 x £2, 10 x £1, 10 x 50p

Will fail if you provide denominations which do not match the currency (Currently only GBP)
Denominations should be provided as pence value i.e 200 = £2

When providing initialization denominations, can also provide runForUserInput as true to still run waiting for user input. You can provide false or leave off if you do not wish to run for further user input.

User Command Running
- status
    Prints out the current balance of the cash Float
  
- reset [denominationCounts]
    Overwrites the existing cash float balance. if no denominationCounts is provided, will prompt for entry for each denomination of the selected currency
  
- add [denomination [count] | denominationCounts]
    Adds to the existing cash float balance. If you just supply the denomination it will add one of that type. You can provide the denomination and count as two inputs, you could provide a set of denominationcounts or it will prompt for entry.
  
- transaction [cost denominationCounts]
    Will open a new transaction to add new cash and then provide change in the difference between the value supplied and cost supplied. If you do not provide the cost and denominationCounts provided, it will prompt for entry.
  
- remove valueToDispense | denominationCounts
    Will either remove coins to a value provided or remove the specified set of denominationCounts.

- exit
    Exits the tool
