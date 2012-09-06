# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table component_model (
  name                      varchar(255) not null,
  price_per_unit            bigint,
  constraint pk_component_model primary key (name))
;

create table product_model (
  name                      varchar(255) not null,
  price                     bigint,
  constraint pk_product_model primary key (name))
;

create table unit_model (
  id                        integer not null,
  product_name              varchar(255),
  component_name            varchar(255),
  units                     integer,
  constraint pk_unit_model primary key (id))
;

create sequence component_model_seq;

create sequence product_model_seq;

create sequence unit_model_seq;

alter table unit_model add constraint fk_unit_model_product_1 foreign key (product_name) references product_model (name) on delete restrict on update restrict;
create index ix_unit_model_product_1 on unit_model (product_name);
alter table unit_model add constraint fk_unit_model_component_2 foreign key (component_name) references component_model (name) on delete restrict on update restrict;
create index ix_unit_model_component_2 on unit_model (component_name);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists component_model;

drop table if exists product_model;

drop table if exists unit_model;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists component_model_seq;

drop sequence if exists product_model_seq;

drop sequence if exists unit_model_seq;

