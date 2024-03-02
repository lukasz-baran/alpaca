# Domain facts

The document called `PLAN KONT` contains data about all people (members and not members).

Not all people are members of the association.

## Registry numbers

There are two types of registry numbers - both are optional!:
* registry numbers (PL: numery kartotek)
* old registry numbers

Currently, members should have their unique registry numbers. But database contain also information about old members who did not have their registration numbers. Non-members also do not have their registry numbers!  
Long-time members have also the old registry numbers; at some point of time old (paper) registry numbers were discarded.

Registry numbers start with index 1

## Technical Issues & Pitfalls

### LocalDateTime precision 

JDBC driver for H2 database truncates nanoseconds. So to simplify testing we explicitly do it code.

### DBF files from the legacy app

Q: How to check original database files on newer Windows versions?

A: Install and register: https://learn.microsoft.com/pl-pl/lifecycle/products/microsoft-visual-foxpro-90

