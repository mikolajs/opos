--przyznanie praw do bazy użytkownikowi vregister  ! nie tworzyć w sytemie użytkowników!  
-- tworzę bazę użytkownikiem postgres createdb vregister, loguję się i tworzę użytkownika oraz nadaję prawa
Create user vregister with password '123test456';
GRANT ALL ON DATABASE vregister TO vregister;
