# Microservice "Conveyor"

This is implementation microservice "Conveyor" which is part of application "Credit conveyor"

**Used technologies:**
Java 11, Spring Boot 2.7.6, Open API, Swagger, JUnit 5, Mockito

### API endpoints

**POST**

`/conveyor/offers`

`/conveyor/calculation`

---

**POST `/conveyor/offers`**

Calculate and get all possible loan parameters.  Pre-scoring is also carried out. 

**Request:**
```
{
	"amount": "300000.00",
	"term": "60",
	"firstName": "Anastasiia",
	"lastName": "Zentsova",
	"middleName": "Romanovna",
	"email": "username@domain.com",
	"birthdate": "1990-01-01",
	"passportSeries": "4444",
	"passportNumber": "666666"
}
```

**Response:**

A list of four JSON sorted by descending rate
 ```
[
	{
		"applicationId": 1,
		"requestedAmount": 300000.00,
		"totalAmount": 300000.00,
		"term": 60,
		"monthlyPayment": 6673.33,
		"rate": 12.00,
		"isInsuranceEnabled": false,
		"isSalaryClient": false
	},
        ...
	{
		"applicationId": 4,
		"requestedAmount": 300000.00,
		"totalAmount": 330000.00,
		"term": 60,
		"monthlyPayment": 7076.65,
		"rate": 10.40,
		"isInsuranceEnabled": true,
		"isSalaryClient": true
	}
]
```

---

**POST `/conveyor/calculation`**

Calculate and get loan conditions

**Request:**

```
{
	"amount": "300000.00",
	"term": "60",
	"firstName": "Anastasiia",
	"lastName": "Zentsova",
	"middleName": "Romanovna",
	"gender": "FEMALE",
	"birthdate": "1992-04-02",
	"passportSeries": "4444",
	"passportNumber": "666666",
	"passportIssueDate": "2019-05-31",
	"passportIssueBranch": "100-051",
	"maritalStatus": "MARRIED",
	"dependentAmount": "1",
	"employment": {
		"employmentStatus": "EMPLOYED",
		"employerINN": "366406939734",
		"salary": "100000.00",
		"position": "DEVELOPER",
		"workExperienceTotal": "100",
		"workExperienceCurrent": "51"
	},
	"account": "40819810570000123456",
	"isInsuranceEnabled": "true",
	"isSalaryClient": "true"
}
```

* GenderEnum {MALE, FEMALE, NOT BINARY}
* MaritalStatusEnum {SINGLE, MARRIED, DIVORCED, WIDOWER}
* EmploymentStatus {EMPLOYED, UNEMPLOYED, SELF-EMPLOYED, BUSINESS OWNER}
* PositionEnum {TOP LEVEL MANAGER, MANAGER, DEVELOPER, TRAINEE}

**Response:**

```
{
	"amount": 372949.19,
	"term": 60,
	"monthlyPayment": 5716.51,
	"rate": 5.40,
	"psk": 4.80,
	"isInsuranceEnabled": true,
	"isSalaryClient": true,
	"paymentSchedule": [
		{
			"number": 1,
			"date": "2023-01-16",
			"totalPayment": 5716.51,
			"interestPayment": 1375.89,
			"debtPayment": 4340.62,
			"remainingDebt": 295659.38
		},
		{
			"number": 2,
			"date": "2023-02-16",
			"totalPayment": 5716.51,
			"interestPayment": 1224.76,
			"debtPayment": 4491.75,
			"remainingDebt": 291167.63
		}
		...
	]
}
```
