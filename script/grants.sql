--przyznanie praw do bazy użytkownikowi  ! nie tworzyć w sytemie użytkowników!  
-- tworzę bazę użytkownikiem postgres createdb osp, loguję się i tworzę użytkownika oraz nadaję prawa
Create user osp with password '123test456';
GRANT ALL ON DATABASE osp TO osp;
