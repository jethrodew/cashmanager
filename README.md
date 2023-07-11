# Cash Manager
Example Java CLI application serving as a cash manager in a vending machine.

[ChangeCalculator.java](src/main/java/org/cashmanager/core/calculator/ChangeCalculator.java) 
contains a solution for the coin change problem with limited coin availability. 
This is run as a backup for a much more efficient primary top down change calculation algorithm because it provides a significant performance gain most of the time.

Uses Maven build manager and compiles to single runnable jar (target/cashmanager-jar-with-dependencies.jar)
Java 17
Maven 3.9.3

```
mvn clean install

java --jar target/cashmanager-jar-with-dependencies.jar gbp [denominationCounts [runForUserInput]]
```

## Providing `denomination`
Must provide denominations which match the currency (Currently only GBP)
Denominations should be provided as pence value i.e 200 = £2, 1 = 1p

## Providing `denominationCounts`
When providing `denominationCounts`, the same rules apply for a singular denomination however it should always be formatted per the following, order of denominations does not matter:
```
200:5,100:10,50:10,20:5
or 
100:10,50:10,200:5,20:5

Translates to 5 x £2, 10 x £1, 10 x 50p, 5 x 20p
```



## User Input
If you only provide the currency when launching, the app will automatically go into user input mode and wait for inputs
When providing initialization denominationCounts, can also provide 3rd argument of true `gbp 50:2,20:3 true` to still run with user input. 
You can provide false or leave off if you do not wish to run for further user input.

### Commands:
>status

Prints out the current balance of the cash Float
  

>reset [denominationCounts]

Overwrites the existing cash float balance. if no denominationCounts is provided, will prompt for entry for each denomination of the selected currency
  

>add [denomination [count] | denominationCounts]

Adds to the existing cash float balance. If you just supply the denomination it will add one of that type. You can provide the denomination and count as two inputs, you could provide a set of denominationcounts or it will prompt for entry.
  

>transaction [cost denominationCounts]

Will open a new transaction to add new cash and then provide change in the difference between the value supplied and cost supplied. If you do not provide the cost and denominationCounts provided, it will prompt for entry.
  

>remove <valueToDispense | denominationCounts>

Will either remove coins with the sum of the total value provided or remove the specified set of denominationCounts.


>exit

Exits the app
