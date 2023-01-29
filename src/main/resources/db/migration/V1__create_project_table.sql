CREATE TABLE project (
  id BIGSERIAL primary key,
  description varchar(2000) NOT NULL,
  technologies_used varchar(300) NOT NULL,
  my_role varchar(255) NOT NULL,
  talent_id bigint(20) NOT NULL,
  CONSTRAINT fk_talent_id FOREIGN KEY(talent_id) REFERENCES talent(id)
 )