# [Introduction](#introduction)
This project has the goal of going through the fundamentals of Relational Database Management System (RDBMS) and
Structured query language (SQL). By creating a database that was deployed by a Docker container it allows for
a safe environment to operate a PostgreSQL database. A script is provided that instantiates a database that allows for queries
to be executed that show the different uses of SQL, this is then pushed to GitHub through using git.


# [SQL Queries](#sql-queries)

###### Table Setup (DDL)
``` 
CREATE TABLE cd.members (
  memid integer NOT NULL, 
  surname character varying(200) NOT NULL, 
  firstname character varying(200) NOT NULL, 
  address character varying(300) NOT NULL, 
  zipcode integer NOT NULL, 
  telephone character varying(20) NOT NULL, 
  recommendedby integer, 
  joindate timestamp NOT NULL, 
  CONSTRAINT members_pk PRIMARY KEY (memid), 
  CONSTRAINT fk_members_recommendedby FOREIGN KEY (recommendedby) REFERENCES cd.members(memid) ON DELETE 
  SET 
    NULL
);
CREATE TABLE cd.facilities (
  facid integer NOT NULL, 
  name character varying(100) NOT NULL, 
  membercost numeric NOT NULL, 
  guestcost numeric NOT NULL, 
  initialoutlay numeric NOT NULL, 
  monthlymaintenance numeric NOT NULL, 
  CONSTRAINT facilities_pk PRIMARY KEY (facid)
);
CREATE TABLE cd.bookings (
  bookid integer NOT NULL, 
  facid integer NOT NULL, 
  memid integer NOT NULL, 
  starttime timestamp NOT NULL, 
  slots integer NOT NULL, 
  CONSTRAINT bookings_pk PRIMARY KEY (bookid), 
  CONSTRAINT fk_bookings_facid FOREIGN KEY (facid) REFERENCES cd.facilities(facid), 
  CONSTRAINT fk_bookings_memid FOREIGN KEY (memid) REFERENCES cd.members(memid)
);
```
## [Modifying Data](#modifying-data)
###### Question 1: Insert some data into a table:
``` 
insert into cd.facilities (
  facid, name, membercost, guestcost, 
  initialoutlay, monthlymaintenance
);
 ```
###### Question 2: Insert calculated data into a table:
``` 
insert into cd.facilities
    (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
    values (9, 'Spa', 20, 30, 100000, 800);
```
###### Question 3: Update some existing data:
``` 
update
    cd.facilities
set
    initialoutlay = 10000
where
    facid = 1;
```
###### Question 4: Update a row based on the contents of another row:
``` 
update
    cd.facilities facs
set
    membercost = (
        select
            membercost * 1.1
        from
            cd.facilities
        where
            facid = 0
    ),
    guestcost = (
        select
            guestcost * 1.1
        from
            cd.facilities
        where
            facid = 0
    )
where
    facs.facid = 1;
```
###### Question 5: Delete all bookings
``` 
delete from
    cd.bookings;
```
###### Question 6: Delete a member from the cd.members table:
``` 
delete from
    cd.members
where
    memid = 37;
```


## [Basics](#basics)
###### Question 1: Control which rows are retrieved:
``` 
select
    facid,
    name,
    membercost,
    monthlymaintenance
from
    cd.facilities
where
    membercost > 0
  and (
    membercost < monthlymaintenance / 50.0
    );
```
###### Question 2: Basic string searches:
``` 
select
    *
from
    cd.facilities
where
    name like '%Tennis%';
```
###### Question 3: Matching against multiple possible values:
``` 
select
    *
from
    cd.facilities
where
    facid in(1, 5);
```
###### Question 4: Working with dates:
```
 select
    memid,
    surname,
    firstname,
    joindate
from
    cd.members
where
    joindate >= '2012-09-01';
```
###### Question 5: Combining results from multiple queries:
``` 
select
    surname
from
    cd.members
union
select
    name
from
    cd.facilities;
```

## [Join](#join)
###### Question 1: Retrieve the start times of members' bookings:
``` 
select
    starttime
from
    cd.bookings bks
        inner join cd.members mems on mems.memid = bks.memid
where
    mems.firstname = 'David'
  and mems.surname = 'Farrell';
```
###### Question 2: Work out the start times of bookings for tennis courts:
``` 
select
    bks.starttime as start,
    facs.name as name
from
    cd.facilities facs
        inner join cd.bookings bks on facs.facid = bks.facid
where
    facs.name in (
                  'Tennis Court 2', 'Tennis Court 1'
        )
  and bks.starttime >= '2012-09-21'
  and bks.starttime < '2012-09-22'
order by
    bks.starttime;
```
###### Question 3: Produce a list of all members, along with their recommender:
``` 
select
    mems.firstname as memfname,
    mems.surname as memsname,
    recs.firstname as recfname,
    recs.surname as recsname
from
    cd.members mems
        left outer join cd.members recs on recs.memid = mems.recommendedby
order by
    memsname,
    memfname;
```
###### Question 4: Produce a list of all members who have recommended another member:
``` 
select
    distinct recs.firstname as firstname,
             recs.surname as surname
from
    cd.members mems
        inner join cd.members recs on recs.memid = mems.recommendedby
order by
    surname,
    firstname;
```
###### Question 5: Produce a list of all members, along with their recommender, using no joins:
``` 
select
    distinct mems.firstname || ' ' || mems.surname as member,
             (
                 select
                     recs.firstname || ' ' || recs.surname as recommender
                 from
                     cd.members recs
                 where
                     recs.memid = mems.recommendedby
             )
from
    cd.members mems
order by
    member;
```

## [Aggregation](#aggregation)
###### Question 1: Count the number of recommendations each member makes:
``` 
select
    recommendedby,
    count(*)
from
    cd.members
where
    recommendedby is not null
group by
    recommendedby
order by
    recommendedby;
```
###### Question 2: List the total slots booked per facility:
``` 
select
    facid,
    sum(slots) as "Total Slots"
from
    cd.bookings
group by
    facid
order by
    facid;
```
###### Question 3: List the total slots booked per facility in a given month:
``` 
select
    facid,
    sum(slots) as "Total Slots"
from
    cd.bookings
where
    starttime >= '2012-09-01'
  and starttime < '2012-10-01'
group by
    facid
order by
    sum(slots) ASC;
```
###### Question 4: List the total slots booked per facility per month:
``` 
select
    facid,
    extract(
            month
            from
            starttime
    ) as month,
  sum(slots) as "Total Slots"
from
    cd.bookings
where
    extract(
    year
    from
    starttime
    ) = 2012
group by
    facid,
    month
order by
    facid,
    month;
```
###### Question 5: Find the count of members who have made at least one booking:
``` 
select
    count(distinct memid)
from
    cd.bookings;
```
###### Question 6: List each member's first booking after September 1st 2012:
``` 
select
    mems.surname,
    mems.firstname,
    mems.memid,
    min(bks.starttime) as starttime
from
    cd.bookings bks
        inner join cd.members mems on mems.memid = bks.memid
where
    starttime >= '2012-09-01'
group by
    mems.surname,
    mems.firstname,
    mems.memid
order by
    mems.memid;
```
###### Question 7: Produce a list of member names, with each row containing the total member count:
``` 
select 
  count(*) over(), 
  firstname, 
  surname 
from 
  cd.members 
order by 
  joindate;

```
###### Question 8: Produce a numbered list of members:
``` 
select 
  row_number() over(
    order by 
      joindate
  ), 
  firstname, 
  surname 
from 
  cd.members 
order by 
  joindate;
```
###### Question 9: Output the facility id that has the highest number of slots booked, again:
``` 
select 
  facid, 
  total 
from 
  (
    select 
      facid, 
      sum(slots) total, 
      rank() over (
        order by 
          sum(slots) desc
      ) rank 
    from 
      cd.bookings 
    group by 
      facid
  ) as ranked 
where 
  rank = 1;

```

## [Strings](#strings)
###### Question 1: Format the names of members:
``` 
select 
  surname || ', ' || firstname as name 
from 
  cd.members;
```
###### Question 2: Find telephone numbers with parentheses:
``` 
select 
  memid, 
  telephone 
from 
  cd.members 
where 
  telephone ~ '[()]';

```
###### Question 3: Count the number of members whose surname starts with each letter of the alphabet:
``` 
select
    substr (mems.surname, 1, 1) as letter,
    count(*) as count
from
    cd.members mems
group by
    letter
order by
    letter;
```
