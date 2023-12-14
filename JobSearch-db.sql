CREATE TABLE USER (
    userID SERIAL NOT NULL,
    pwHASH INTEGER NOT NULL,
    fname varchar(20) NOT NULL,
    lname varchar(20) NOT NULL,
    school varchar(50)
    PRIMARY KEY (userID),

);

CREATE TABLE COMPANY(
    companyID SERIAL NOT NULL,
    cName varchar(50) NOT NULL,
    incDate DATE NOT NULL,
    empCount INTEGER NOT NULL,
    locations varchar(50) NOT NULL,
    about varchar (50)
    PRIMARY KEY (companyID),

);


CREATE TABLE ADVERTISEMENT (
    adID SERIAL NOT NULL,
    companyID INTEGER NOT NULL,
    jobTitle VARCHAR(30) NOT NULL,
    pubDate DATE NOT NULL,
    experience varchar(20)
    PRIMARY KEY (adID),
    FOREIGN KEY (companyID) REFERENCES COMPANY(companyID)
);


CREATE TABLE CERTIFICATE(
    cerID SERIAL NOT NULL,
    companyID INTEGER NOT NULL,
    cerName varchar(50) NOT NULL,
    PRIMARY KEY (cerID),
    FOREIGN KEY (companyID) REFERENCES TO COMPANY(companyID)
);