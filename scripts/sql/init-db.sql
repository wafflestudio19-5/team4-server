CREATE DATABASE reddit;
CREATE DATABASE reddit_test;
CREATE USER 'waffle-team-4'@'localhost' IDENTIFIED BY 'waffleteam4';
GRANT ALL PRIVILEGES ON reddit.* TO 'waffle-team-4'@'localhost';
GRANT ALL PRIVILEGES ON reddit_test.* TO 'waffle-team-4'@'localhost';
FLUSH PRIVILEGES ;
