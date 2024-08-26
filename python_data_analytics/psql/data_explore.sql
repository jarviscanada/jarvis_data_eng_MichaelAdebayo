
-- Q1: Show first 10 rows
SELECT
    *
FROM
    retail
        LIMIT
    10;

-- Q2: Check # of records
SELECT
    COUNT(*)
FROM
    retail;

-- Q3: Number of clients (e.g. unique client ID)
SELECT
    COUNT(DISTINCT customer_id)
FROM
    retail;

-- Q4: Invoice date range (e.g. max/min dates)
SELECT
    MAX(invoice_date),
    MIN(invoice_date)
FROM
    retail;

-- Q5: Number of SKU/merchants (e.g. unique stock code)
SELECT
    COUNT(DISTINCT stock_code)
FROM
    retail;

-- Q6: Calculate average invoice amount excluding invoices with a negative amount (e.g. canceled orders have negative amount)
SELECT
    AVG(invoice_total) AS avg
FROM
    (
    SELECT
    SUM(quantity * unit_price) AS invoice_total
    FROM
    retail
    GROUP BY
    invoice_no
    HAVING
    SUM(quantity * unit_price) > 0
    ) AS valid_invoices;

-- Q7: Calculate total revenue (e.g. sum of unit_price * quantity)
SELECT
    SUM (quantity * unit_price) as sum
FROM
    retail;

-- Q8: Calculate total revenue by YYYYMM
SELECT
    TO_CHAR(invoice_date, 'YYYYMM') AS year_month,
    SUM(quantity * unit_price) AS total_revenue
FROM
    retail
GROUP BY
    TO_CHAR(invoice_date, 'YYYYMM')
ORDER BY
    year_month;


