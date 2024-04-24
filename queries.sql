-- Create Companies Table
CREATE TABLE Companies (
    CompanyID SERIAL NOT NULL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Industry VARCHAR(255) NOT NULL,
    Location VARCHAR(255) NOT NULL,
    Description TEXT
);

-- Create Users Table
CREATE TABLE Users (
    UserID SERIAL NOT NULL PRIMARY KEY,
    Username VARCHAR(255) NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL,
    FullName VARCHAR(255) NOT NULL,
    Location VARCHAR(255) ,
    ProfileSummary TEXT,
    ExperienceLevel VARCHAR(255),
    Education TEXT,
    Skills TEXT
);

ALTER TABLE Users ADD CONSTRAINT password_hash_size_check CHECK (LENGTH(PasswordHash) >= 6);
-- Create Jobs Table
CREATE TABLE Jobs (
    JobID SERIAL PRIMARY KEY,
    EmployerID INT NOT NULL,
    Title VARCHAR(255) NOT NULL,
    Description TEXT,
    Location VARCHAR(255) NOT NULL,
    Type VARCHAR(100),
    Category VARCHAR(100) NOT NULL,
    PostedDate DATE NOT NULL,
    FOREIGN KEY (EmployerID) REFERENCES Companies(CompanyID) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Create Applications Table
CREATE TABLE Applications (
    ApplicationID SERIAL,
    JobID INT NOT NULL,
    UserID INT NOT NULL,
    ApplicationDate DATE NOT NULL,
    Status VARCHAR(100) NOT NULL,
	PRIMARY KEY(JobID,UserID),
    FOREIGN KEY (JobID) REFERENCES Jobs(JobID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON UPDATE CASCADE ON DELETE CASCADE

);

-- Existing tables remain unchanged (Companies, Users, Jobs, Applications)

-- Create Certificates Table
CREATE TABLE Certificates (
    CertificateID SERIAL PRIMARY KEY,
    CompanyID INT NOT NULL DEFAULT 0,
    Name VARCHAR(255),
    Description TEXT,
    Duration VARCHAR(100),
    SkillsAcquired TEXT,
    FOREIGN KEY (CompanyID) REFERENCES Companies(CompanyID) ON UPDATE CASCADE ON DELETE SET DEFAULT 
);

-- Create UserCertificates Table
CREATE TABLE UserCertificates (
    UserCertificateID SERIAL ,
    UserID INT,
    CertificateID INT DEFAULT 0,
    EnrollmentDate DATE,
    CompletionDate DATE,
    Status VARCHAR(100),
	PRIMARY KEY (UserID,CertificateID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (CertificateID) REFERENCES Certificates(CertificateID) ON UPDATE CASCADE ON DELETE SET DEFAULT
);


CREATE OR REPLACE FUNCTION check_password_length()
RETURNS TRIGGER AS $$
BEGIN
    IF LENGTH(NEW.PasswordHash) < 6 THEN
        RAISE EXCEPTION 'Password en az 6 karakter olmalidir';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_password_length_trigger
BEFORE INSERT ON Users
FOR EACH ROW EXECUTE FUNCTION check_password_length();




CREATE OR REPLACE FUNCTION check_company_exists()
RETURNS TRIGGER AS $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM Companies WHERE CompanyID = NEW.EmployerID) THEN
    RAISE EXCEPTION 'CompanyID does not exist';
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_company_before_insert
BEFORE INSERT ON Jobs
FOR EACH ROW EXECUTE FUNCTION check_company_exists();

CREATE OR REPLACE FUNCTION check_user_job_exists()
RETURNS TRIGGER AS $$
BEGIN
  -- Check if UserID exists in Users table
  IF NOT EXISTS (SELECT 1 FROM Users WHERE UserID = NEW.UserID) THEN
    RAISE EXCEPTION 'UserID does not exist';
  END IF;
  -- Check if JobID exists in Jobs table
  IF NOT EXISTS (SELECT 1 FROM Jobs WHERE JobID = NEW.JobID) THEN
    RAISE EXCEPTION 'JobID does not exist';
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_user_job_before_insert
BEFORE INSERT ON Applications
FOR EACH ROW EXECUTE FUNCTION check_user_job_exists();

CREATE OR REPLACE FUNCTION check_user_certificate_exists()
RETURNS TRIGGER AS $$
BEGIN
  -- Check if UserID exists in Users table
  IF NOT EXISTS (SELECT 1 FROM Users WHERE UserID = NEW.UserID) THEN
    RAISE EXCEPTION 'UserID does not exist';
  END IF;
  -- Check if CertificateID exists in Certificates table
  IF NOT EXISTS (SELECT 1 FROM Certificates WHERE CertificateID = NEW.CertificateID) THEN
    RAISE EXCEPTION 'CertificateID does not exist';
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_user_certificate_before_insert
BEFORE INSERT ON UserCertificates
FOR EACH ROW EXECUTE FUNCTION check_user_certificate_exists();


CREATE OR REPLACE FUNCTION trig_basvuru()
RETURNS TRIGGER AS $$
DECLARE
    my_row Applications%ROWTYPE;
BEGIN
    FOR my_row IN SELECT * FROM Applications WHERE jobID = NEW.jobID AND userID = NEW.userID LOOP
        RAISE EXCEPTION 'Bu ise daha once basvurdunuz';
        RETURN NULL;
    END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER trigger_basvuru
BEFORE INSERT ON Applications
FOR EACH ROW EXECUTE PROCEDURE trig_basvuru();



CREATE SEQUENCE Applications_id_seq START 1;
ALTER TABLE applications
ALTER COLUMN ApplicationID SET DEFAULT nextval('Applications_id_seq');
	

CREATE VIEW allJobAds 
AS 	
SELECT JobID, Title, Description, Location, Type, Category, PostedDate
FROM Jobs;
	
	
	
	
INSERT INTO Companies (Name, Industry, Location, Description) VALUES
('ABC Company', 'Technology', 'New York', 'Leading tech company specializing in software development.'),
('XYZ Corporation', 'Finance', 'London', 'Global financial services provider.'),
('Tech Innovators', 'Technology', 'San Francisco', 'Innovative technology solutions provider.'),
('Global Motors', 'Automotive', 'Detroit', 'Leading car manufacturing company.'),
('Green Energy Solutions', 'Energy', 'Berlin', 'Renewable energy solutions for a sustainable future.'),
('Fashion Trends', 'Fashion', 'Paris', 'Trendsetting fashion company.'),
('Healthcare Innovations', 'Healthcare', 'Boston', 'Revolutionizing healthcare with cutting-edge solutions.'),
('Creative Studios', 'Media', 'Los Angeles', 'Multimedia content creation and production.'),
('Food Delights', 'Food', 'Tokyo', 'Delicious and diverse food products.'),
('Green Earth Foundation', 'Environment', 'Amsterdam', 'Working towards a greener and healthier planet.'),
('Innovative Robotics', 'Technology', 'Seoul', 'Pioneering advancements in robotics.'),
('Global Consultants', 'Business', 'Singapore', 'Strategic consulting services for businesses worldwide.'),
('Space Explorations', 'Aerospace', 'Houston', 'Exploring the cosmos with advanced aerospace technology.'),
('Travel Adventures', 'Travel', 'Sydney', 'Creating unforgettable travel experiences worldwide.'),
('Educational Insights', 'Education', 'Toronto', 'Empowering education through innovative solutions.'),
('Sports Dynamics', 'Sports', 'Rio de Janeiro', 'Promoting sports excellence and fitness.'),
('Urban Architects', 'Architecture', 'Dubai', 'Designing the future with architectural innovations.'),
('Oceanic Discoveries', 'Marine', 'Cape Town', 'Exploring the wonders of the ocean with marine research.'),
('Global Pharma Solutions', 'Pharmaceuticals', 'Mumbai', 'Advancing healthcare through pharmaceutical innovations.'),
('Artistic Creations', 'Art', 'Barcelona', 'Celebrating creativity through diverse artistic expressions.');



INSERT INTO Users (Username, PasswordHash, Email, FullName, Location, ProfileSummary, ExperienceLevel, Education, Skills) VALUES
('john_doe', 'hashed_password_1', 'john.doe@email.com', 'John Doe', 'New York', 'Experienced software developer', 'Senior', 'BS in Computer Science', 'Java, Python, JavaScript'),
('jane_smith', 'hashed_password_2', 'jane.smith@email.com', 'Jane Smith', 'London', 'Finance professional with a global perspective', 'Mid-level', 'MBA in Finance', 'Financial Analysis, Risk Management, Excel'),
('tech_enthusiast', 'hashed_password_3', 'tech.enthusiast@email.com', 'Tech Enthusiast', 'San Francisco', 'Passionate about the latest tech trends', 'Entry-level', 'B.Tech in Computer Engineering', 'Programming, Machine Learning, Cloud Computing'),
('auto_engineer', 'hashed_password_4', 'auto.engineer@email.com', 'Auto Engineer', 'Detroit', 'Automotive engineering expert', 'Senior', 'MS in Mechanical Engineering', 'Automotive Design, CAD, Manufacturing'),
('renewable_energy_pro', 'hashed_password_5', 'renewable.energy@email.com', 'Renewable Energy Pro', 'Berlin', 'Dedicated to sustainable energy solutions', 'Mid-level', 'Ph.D. in Renewable Energy', 'Solar Energy, Wind Power, Energy Policy'),
('fashionista', 'hashed_password_6', 'fashionista@email.com', 'Fashionista', 'Paris', 'Setting fashion trends with style', 'Entry-level', 'Fashion Design Degree', 'Fashion Styling, Pattern Making, Trend Analysis'),
('healthcare_innovator', 'hashed_password_7', 'healthcare.innovator@email.com', 'Healthcare Innovator', 'Boston', 'Innovating healthcare for a healthier world', 'Senior', 'MD in Medicine', 'Medical Research, Healthcare Management, Public Health'),
('media_creator', 'hashed_password_8', 'media.creator@email.com', 'Media Creator', 'Los Angeles', 'Creative mind behind multimedia content', 'Mid-level', 'Bachelor in Film Production', 'Video Editing, Graphic Design, Cinematography'),
('food_enthusiast', 'hashed_password_9', 'food.enthusiast@email.com', 'Food Enthusiast', 'Tokyo', 'Exploring diverse and delicious cuisines', 'Entry-level', 'Culinary Arts Diploma', 'Culinary Skills, Food Photography, Menu Planning'),
('environmentalist', 'hashed_password_10', 'environmentalist@email.com', 'Environmentalist', 'Amsterdam', 'Passionate about environmental conservation', 'Senior', 'Ph.D. in Environmental Science', 'Sustainability, Conservation, Climate Change'),
('robotics_expert', 'hashed_password_11', 'robotics.expert@email.com', 'Robotics Expert', 'Seoul', 'Leading advancements in robotics technology', 'Mid-level', 'MS in Robotics Engineering', 'Robotics Programming, Automation, AI'),
('business_consultant', 'hashed_password_12', 'business.consultant@email.com', 'Business Consultant', 'Singapore', 'Strategic consulting for global businesses', 'Senior', 'MBA in Business Administration', 'Strategic Planning, Market Analysis, Business Development'),
('space_enthusiast', 'hashed_password_13', 'space.enthusiast@email.com', 'Space Enthusiast', 'Houston', 'Passionate about exploring the cosmos', 'Entry-level', 'B.Sc. in Astrophysics', 'Space Exploration, Astronomy, Astrophotography'),
('travel_adventurer', 'hashed_password_14', 'travel.adventurer@email.com', 'Travel Adventurer', 'Sydney', 'Creating unforgettable travel experiences', 'Mid-level', 'Tourism and Travel Management', 'Destination Planning, Travel Photography, Cultural Awareness'),
('education_innovator', 'hashed_password_15', 'education.innovator@email.com', 'Education Innovator', 'Toronto', 'Empowering education through innovation', 'Senior', 'Ph.D. in Education', 'Educational Technology, Curriculum Development, Teacher Training'),
('sports_enthusiast', 'hashed_password_16', 'sports.enthusiast@email.com', 'Sports Enthusiast', 'Rio de Janeiro', 'Promoting sports excellence and fitness', 'Entry-level', 'Bachelor in Sports Science', 'Athlete Training, Sports Nutrition, Sports Psychology'),
('architectural_designer', 'hashed_password_17', 'architectural.designer@email.com', 'Architectural Designer', 'Dubai', 'Designing the future with architectural innovations', 'Mid-level', 'Master in Architecture', 'Architectural Design, 3D Modeling, Urban Planning'),
('marine_explorer', 'hashed_password_18', 'marine.explorer@email.com', 'Marine Explorer', 'Cape Town', 'Exploring the wonders of the ocean', 'Senior', 'Oceanography Degree', 'Marine Biology, Ocean Exploration, Conservation'),
('pharma_scientist', 'hashed_password_19', 'pharma.scientist@email.com', 'Pharma Scientist', 'Mumbai', 'Advancing healthcare through pharmaceuticals', 'Mid-level', 'Pharmaceutical Sciences', 'Drug Development, Pharmacology, Clinical Trials'),
('123', '123456', 'art.creator@email.com', 'Art Creator', 'Barcelona', 'Celebrating creativity through diverse artistic expressions', 'Entry-level', 'Fine Arts Degree', 'Painting, Sculpture, Digital Art');



INSERT INTO Jobs (EmployerID, Title, Description, Location, Type, Category, PostedDate) VALUES
(1, 'Senior Software Developer', 'Lead a team of developers to create cutting-edge software solutions.', 'New York', 'Full-time', 'Technology', '2024-01-06'),
(2, 'Financial Analyst', 'Conduct financial analysis and provide insights for strategic decision-making.', 'London', 'Full-time', 'Finance', '2024-01-06'),
(3, 'Machine Learning Engineer', 'Develop machine learning models for innovative tech applications.', 'San Francisco', 'Full-time', 'Technology', '2024-01-06'),
(4, 'Automotive Design Engineer', 'Lead the design team in creating the next generation of cars.', 'Detroit', 'Full-time', 'Automotive', '2024-01-06'),
(5, 'Renewable Energy Specialist', 'Research and implement sustainable energy solutions for the future.', 'Berlin', 'Full-time', 'Energy', '2024-01-06'),
(6, 'Fashion Stylist', 'Set trends and create unique fashion styles for various projects.', 'Paris', 'Part-time', 'Fashion', '2024-01-06'),
(7, 'Healthcare Researcher', 'Conduct research to advance medical knowledge and improve healthcare.', 'Boston', 'Full-time', 'Healthcare', '2024-01-06'),
(8, 'Multimedia Content Producer', 'Produce engaging multimedia content for diverse audiences.', 'Los Angeles', 'Full-time', 'Media', '2024-01-06'),
(9, 'Culinary Specialist', 'Explore and create diverse and delicious food experiences.', 'Tokyo', 'Part-time', 'Food', '2024-01-06'),
(10, 'Environmental Conservationist', 'Work towards a greener and healthier planet through conservation efforts.', 'Amsterdam', 'Full-time', 'Environment', '2024-01-06'),
(11, 'Robotics Programmer', 'Program and develop innovative robotic systems for various applications.', 'Seoul', 'Full-time', 'Technology', '2024-01-06'),
(12, 'Strategic Business Consultant', 'Provide strategic consulting services to global businesses.', 'Singapore', 'Full-time', 'Business', '2024-01-06'),
(13, 'Astrophysicist', 'Study the cosmos and contribute to advancements in astrophysics.', 'Houston', 'Full-time', 'Aerospace', '2024-01-06'),
(14, 'Travel Experience Curator', 'Curate and plan unforgettable travel experiences for clients.', 'Sydney', 'Part-time', 'Travel', '2024-01-06'),
(15, 'Educational Technology Innovator', 'Innovate and implement technology in education for better learning outcomes.', 'Toronto', 'Full-time', 'Education', '2024-01-06'),
(16, 'Sports Performance Coach', 'Train athletes and promote sports excellence and fitness.', 'Rio de Janeiro', 'Full-time', 'Sports', '2024-01-06'),
(17, 'Architectural Designer', 'Design innovative architectural structures and urban plans.', 'Dubai', 'Full-time', 'Architecture', '2024-01-06'),
(18, 'Marine Biologist', 'Explore and study marine life for conservation and scientific research.', 'Cape Town', 'Full-time', 'Marine', '2024-01-06'),
(19, 'Pharmaceutical Research Scientist', 'Contribute to pharmaceutical advancements through research and development.', 'Mumbai', 'Full-time', 'Pharmaceuticals', '2024-01-06'),
(20, 'Visual Artist', 'Express creativity through various artistic mediums and create unique artworks.', 'Barcelona', 'Part-time', 'Art', '2024-01-06');

	
INSERT INTO Applications (JobID, UserID, ApplicationDate, Status) VALUES
(1, 3, '2024-01-07', 'Pending'),
(2, 7, '2024-01-08', 'Pending'),
(3, 11, '2024-01-09', 'Pending'),
(4, 16, '2024-01-10', 'Pending'),
(5, 20, '2024-01-11', 'Pending'),
(6, 5, '2024-01-12', 'Pending'),
(7, 1, '2024-01-13', 'Pending'),
(8, 13, '2024-01-14', 'Pending'),
(9, 18, '2024-01-15', 'Pending'),
(10, 9, '2024-01-16', 'Pending'),
(11, 2, '2024-01-17', 'Pending'),
(12, 15, '2024-01-18', 'Pending'),
(13, 10, '2024-01-19', 'Pending'),
(14, 19, '2024-01-20', 'Pending'),
(15, 8, '2024-01-21', 'Pending'),
(16, 12, '2024-01-22', 'Pending'),
(17, 17, '2024-01-23', 'Pending'),
(18, 14, '2024-01-24', 'Pending'),
(19, 4, '2024-01-25', 'Pending'),
(20, 6, '2024-01-26', 'Pending');



INSERT INTO Certificates (CompanyID, Name, Description, Duration, SkillsAcquired) VALUES
(1, 'Java Developer Certification', 'Certification for Java programming proficiency.', '3 months', 'Java Programming, Software Development'),
(2, 'Finance Analytics Specialist', 'Specialized certification in financial analytics.', '2 months', 'Financial Analysis, Data Analytics, Excel'),
(3, 'Machine Learning Fundamentals', 'Fundamental concepts of machine learning.', '4 months', 'Machine Learning Basics, Data Science'),
(4, 'Automotive Design Mastery', 'Master the art of automotive design principles.', '6 months', 'Automotive Design, CAD Modeling'),
(5, 'Renewable Energy Technologies', 'Understanding and implementing renewable energy technologies.', '5 months', 'Renewable Energy, Sustainability'),
(6, 'Fashion Styling Techniques', 'Techniques and trends in fashion styling.', '3 months', 'Fashion Styling, Trend Analysis'),
(7, 'Healthcare Management Certification', 'Certification in healthcare management practices.', '4 months', 'Healthcare Management, Medical Administration'),
(8, 'Multimedia Content Creation', 'Creating engaging multimedia content.', '6 months', 'Video Production, Graphic Design'),
(9, 'Culinary Arts Mastery', 'Mastering the art and science of culinary arts.', '8 months', 'Culinary Techniques, Menu Planning'),
(10, 'Environmental Conservation Strategies', 'Strategies for effective environmental conservation.', '5 months', 'Environmental Conservation, Sustainability'),
(11, 'Advanced Robotics Programming', 'Advanced programming for robotics systems.', '6 months', 'Robotics Programming, AI'),
(12, 'Strategic Business Consulting', 'Strategies and tactics for strategic business consulting.', '4 months', 'Business Strategy, Consulting'),
(13, 'Astronomy and Astrophysics', 'Exploring the wonders of astronomy and astrophysics.', '7 months', 'Astronomy, Astrophysics'),
(14, 'Travel Planning and Management', 'Planning and managing travel experiences.', '3 months', 'Travel Planning, Tourism Management'),
(15, 'Educational Technology Innovations', 'Innovative technologies in education.', '5 months', 'Educational Technology, E-Learning'),
(16, 'Sports Performance Coaching', 'Coaching techniques for sports performance enhancement.', '6 months', 'Sports Coaching, Fitness Training'),
(17, 'Architectural Design Principles', 'Principles and practices of architectural design.', '8 months', 'Architectural Design, Urban Planning'),
(18, 'Marine Biology Fundamentals', 'Fundamental concepts of marine biology.', '4 months', 'Marine Biology, Oceanography'),
(19, 'Pharmaceutical Research and Development', 'Research and development in pharmaceuticals.', '7 months', 'Pharmaceutical Research, Drug Development'),
(20, 'Visual Arts Exploration', 'Exploring various forms of visual arts.', '6 months', 'Painting, Sculpture, Digital Art');



INSERT INTO UserCertificates (UserID, CertificateID, EnrollmentDate, CompletionDate, Status) VALUES
(3, 1, '2024-01-07', '2024-04-07', 'Completed'),
(7, 2, '2024-01-08', '2024-03-08', 'Completed'),
(11, 3, '2024-01-09', '2024-05-09', 'In Progress'),
(16, 4, '2024-01-10', NULL, 'Enrolled'),
(20, 5, '2024-01-11', NULL, 'Enrolled'),
(5, 6, '2024-01-12', '2024-06-12', 'Completed'),
(1, 7, '2024-01-13', '2024-05-13', 'In Progress'),
(13, 8, '2024-01-14', '2024-07-14', 'Completed'),
(18, 9, '2024-01-15', NULL, 'Enrolled'),
(9, 10, '2024-01-16', '2024-09-16', 'Completed'),
(2, 11, '2024-01-17', '2024-04-17', 'Completed'),
(15, 12, '2024-01-18', '2024-05-18', 'In Progress'),
(10, 13, '2024-01-19', '2024-08-19', 'Completed'),
(19, 14, '2024-01-20', NULL, 'Enrolled'),
(8, 15, '2024-01-21', '2024-06-21', 'Completed'),
(12, 16, '2024-01-22', '2024-07-22', 'Completed'),
(17, 17, '2024-01-23', NULL, 'Enrolled'),
(14, 18, '2024-01-24', '2024-09-24', 'Completed'),
(4, 19, '2024-01-25', '2024-08-25', 'Completed'),
(6, 20, '2024-01-26', NULL, 'Enrolled');

CREATE OR REPLACE FUNCTION getJobsByCompany(companyName VARCHAR)
RETURNS TABLE (
    jobID INT,
    title VARCHAR(255),
    description TEXT,
    location VARCHAR(255),
    type VARCHAR(100),
    category VARCHAR(100),
    postedDate DATE
)
AS $$
BEGIN
    RETURN QUERY
    SELECT
        j.JobID,
        j.Title,
        j.Description,
        j.Location,
        j.Type,
        j.Category,
        j.PostedDate
    FROM
        Jobs j
    JOIN
        Companies c ON j.EmployerID = c.CompanyID
    WHERE
        c.Name = companyName;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION get_jobs_by_location_and_industry(input_location VARCHAR(255), input_industry VARCHAR(255))
RETURNS TABLE (
    JobID INT,
    CompanyName VARCHAR(255),
    Title VARCHAR(255),
    JobDescription TEXT,
    JobLocation VARCHAR(255),
    JobType VARCHAR(100),
    JobCategory VARCHAR(100),
    PostedDate DATE
) AS $$
DECLARE
    job_record RECORD;
    job_cursor CURSOR FOR 
        SELECT 
            j.JobID,
            c.Name,
            j.Title,
            j.Description,
            j.Location,
            j.Type,
            j.Category,
            j.PostedDate
        FROM 
            Jobs j
        JOIN 
            Companies c ON j.EmployerID = c.CompanyID
        WHERE 
            j.Location = input_location AND c.Industry = input_industry;
BEGIN
    OPEN job_cursor;
    LOOP
        FETCH job_cursor INTO job_record;
        EXIT WHEN NOT FOUND;
        JobID := job_record.JobID;
        CompanyName := job_record.Name;
        Title := job_record.Title;
        JobDescription := job_record.Description;
        JobLocation := job_record.Location;
        JobType := job_record.Type;
        JobCategory := job_record.Category;
        PostedDate := job_record.PostedDate;
        RETURN NEXT;
    END LOOP;
    CLOSE job_cursor;
END;
$$ LANGUAGE plpgsql;




CREATE OR REPLACE FUNCTION get_user_applications(input_username VARCHAR(255))
RETURNS TABLE (
    ApplicationID INT,
    Title VARCHAR(255),
    Description TEXT,
    Location VARCHAR(255),
    Type VARCHAR(100),
    Category VARCHAR(100),
    PostedDate DATE,
    Status VARCHAR(100)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.ApplicationID,
        j.Title,
        j.Description,
        j.Location,
        j.Type,
        j.Category,
        j.PostedDate,
        a.Status
    FROM 
        Jobs j
    JOIN 
        Applications a ON j.JobID = a.JobID
    JOIN 
        Users u ON a.UserID = u.UserID
    WHERE 
        u.Username = input_username;
END;
$$ LANGUAGE plpgsql;


