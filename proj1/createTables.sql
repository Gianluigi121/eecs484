CREATE TABLE USERS (
    user_id number,
    first_name varchar2(100) NOT NULL,
    last_name varchar2(100) NOT NULL,
    year_of_birth integer,
    month_of_birth integer,
    day_of_birth integer,
    gender varchar2(100),
    PRIMARY KEY(user_id)
);

CREATE TABLE FRIENDS (
    user1_id number,
    user2_id number,
    CHECK(user1_id <> user2_id),
    PRIMARY KEY(user1_id, user2_id),
    FOREIGN KEY(user1_id) REFERENCES USERS(user_id) on delete cascade,
    FOREIGN KEY(user2_id) REFERENCES USERS(user_id) on delete cascade
);

CREATE TABLE CITIES (
    city_id integer,
    city_name varchar2(100) NOT NULL,
    state_name varchar2(100) NOT NULL,
    country_name varchar2(100) NOT NULL,
    PRIMARY KEY(city_id),
    UNIQUE(city_name, state_name, country_name)
);

CREATE TABLE USER_CURRENT_CITIES (
    user_id number,
    current_city_id integer NOT NULL,
    PRIMARY KEY (user_id),
    FOREIGN KEY(user_id) REFERENCES USERS(user_id) on delete cascade,
    FOREIGN KEY(current_city_id) REFERENCES CITIES(city_id) on delete cascade
);

CREATE TABLE USER_HOMETOWN_CITIES (
    user_id number,
    hometown_city_id integer NOT NULL,
    PRIMARY KEY(user_id),
    FOREIGN KEY(user_id) REFERENCES USERS(user_id) on delete cascade,
    FOREIGN KEY(hometown_city_id) REFERENCES CITIES(city_id) on delete cascade
);

CREATE TABLE MESSAGES (
    message_id number,
    sender_id number NOT NULL,
    receiver_id number NOT NULL,
    message_content varchar2(2000) NOT NULL,
    sent_time timestamp NOT NULL,
    PRIMARY KEY(message_id),
    FOREIGN KEY(sender_id) REFERENCES USERS(user_id) on delete cascade,
    FOREIGN KEY(receiver_id) REFERENCES USERS(user_id) on delete cascade
);

CREATE TABLE PROGRAMS (
    program_id integer,
    institution varchar2(100) NOT NULL,
    concentration varchar2(100) NOT NULL,
    degree varchar2(100) NOT NULL,
    PRIMARY KEY(program_id),
    UNIQUE(institution, concentration, degree)
);

CREATE TABLE EDUCATION (
    user_id number,
    program_id integer,
    program_year integer NOT NULL,
    PRIMARY KEY(user_id, program_id),
    FOREIGN KEY(user_id) REFERENCES USERS on delete cascade,
    FOREIGN KEY(program_id) REFERENCES PROGRAMS on delete cascade
);

CREATE TABLE USER_EVENTS (
    event_id number,
    event_creator_id number NOT NULL,
    event_name varchar2(100) NOT NULL,
    event_tagline varchar2(100),
    event_description varchar2(100),
    event_host varchar2(100),
    event_type varchar2(100),
    event_subtype varchar2(100),
    event_address varchar2(2000),
    event_city_id integer NOT NULL,
    event_start_time timestamp,
    event_end_time timestamp,
    PRIMARY KEY(event_id),
    FOREIGN KEY(event_creator_id) REFERENCES USERS(user_id) on delete cascade,
    FOREIGN KEY(event_city_id) REFERENCES CITIES(city_id) on delete cascade
);

CREATE TABLE PARTICIPANTS (
    event_id number NOT NULL,
    user_id number NOT NULL,
    confirmation varchar2(100) NOT NULL,
    CHECK(confirmation = 'ATTENDING' OR confirmation = 'UNSURE' OR confirmation = 'DECLINES' OR confirmation = 'NOT_REPLIED'),
    PRIMARY KEY(event_id, user_id),
    FOREIGN KEY(user_id) REFERENCES USERS on delete cascade,
    FOREIGN KEY(event_id) REFERENCES USER_EVENTS on delete cascade
);

CREATE TABLE ALBUMS (
    album_id number,
    album_owner_id number NOT NULL,
    album_name varchar2(100) NOT NULL,
    album_created_time timestamp NOT NULL,
    album_modified_time timestamp,
    album_link varchar2(100) NOT NULL,
    album_visibility varchar2(100) NOT NULL,
    CHECK (album_visibility = 'EVERYONE' OR album_visibility = 'FRIENDS' OR album_visibility = 'FRIENDS_OF_FRIENDS' OR album_visibility = 'MYSELF'),
    cover_photo_id number NOT NULL,
    PRIMARY KEY(album_id),
    FOREIGN KEY(album_owner_id) REFERENCES USERS(user_id) on delete cascade
);

CREATE TABLE PHOTOS (
    photo_id number,
    album_id number NOT NULL,
    photo_caption varchar2(2000),
    photo_created_time timestamp NOT NULL,
    photo_modified_time timestamp,
    photo_link varchar2(2000) NOT NULL,
    PRIMARY KEY(photo_id)
);

CREATE TABLE TAGS (
    tag_photo_id number,
    tag_subject_id number,
    tag_created_time timestamp NOT NULL,
    tag_x number NOT NULL,
    tag_y number NOT NULL,
    PRIMARY KEY(tag_photo_id, tag_subject_id),
    FOREIGN KEY(tag_photo_id) REFERENCES PHOTOS(photo_id) on delete cascade,
    FOREIGN KEY(tag_subject_id) REFERENCES USERS(user_id) on delete cascade
);


ALTER TABLE ALBUMS
ADD CONSTRAINT MUST_HAVE_COVER_PHOTO
FOREIGN KEY(cover_photo_id) REFERENCES PHOTOS(photo_id)
INITIALLY DEFERRED DEFERRABLE;

ALTER TABLE PHOTOS
ADD CONSTRAINT MUST_HAVE_ALBUM
FOREIGN KEY(album_id) REFERENCES ALBUMS(album_id)
INITIALLY DEFERRED DEFERRABLE;


CREATE TRIGGER order_friends_pairs
    BEFORE INSERT ON FRIENDS
    FOR EACH ROW
        DECLARE temp NUMBER;
        BEGIN
            IF :NEW.USER1_ID > :NEW.USER2_ID THEN
                temp := :NEW.USER2_ID;
                :NEW.USER2_ID := :NEW.USER1_ID;
                :NEW.USER1_ID := temp;
            END IF ;
        END;
/

CREATE SEQUENCE CITY_ID
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER CITY_ID_TRIGGER
    BEFORE INSERT ON CITIES
    FOR EACH ROW
    BEGIN
        SELECT CITY_ID.NEXTVAL INTO :NEW.city_id FROM DUAL;
    END;
/

CREATE SEQUENCE PROGRAM_ID
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER PROGRAM_ID_TRIGGER
    BEFORE INSERT ON PROGRAMS
    FOR EACH ROW
    BEGIN
        SELECT PROGRAM_ID.NEXTVAL INTO :NEW.program_id FROM DUAL;
    END;
/
