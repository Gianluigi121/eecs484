CREATE VIEW VIEW_USER_INFORMATION AS
SELECT U.user_id, U.first_name, U.last_name, U.year_of_birth, U.month_of_birth, U.day_of_birth, U.gender,
CUR.city_name AS CURRENT_CITY, 
CUR.state_name AS CURRENT_STATE,
CUR.country_name AS CURRENT_COUNTRY,
HOM.city_name AS HOMETOWN_CITY, 
HOM.state_name AS HOMETOWN_STATE,
HOM.country_name AS HOMETOWN_COUNTRY,
PROG.institution AS INSTITUTION_NAME,
EDU.program_year,
PROG.concentration AS PROGRAM_CONCENTRATION,
PROG.degree AS PROGRAM_DEGREE
FROM USERS U
LEFT JOIN USER_CURRENT_CITIES UCC ON U.user_id = UCC.user_id
LEFT JOIN CITIES CUR ON UCC.current_city_id = CUR.city_id
LEFT JOIN USER_HOMETOWN_CITIES UHC ON U.user_id = UHC.user_id
LEFT JOIN CITIES HOM ON UHC.hometown_city_id = HOM.city_id
LEFT JOIN EDUCATION EDU ON U.user_id = EDU.user_id
LEFT JOIN PROGRAMS PROG ON EDU.program_id = PROG.program_id;

CREATE VIEW VIEW_ARE_FRIENDS AS
SELECT FRIENDS.user1_id, FRIENDS.user2_id
FROM FRIENDS;

CREATE VIEW VIEW_PHOTO_INFORMATION AS
SELECT ALBUMS.album_id, ALBUMS.album_owner_id, ALBUMS.cover_photo_id, ALBUMS.album_name,
ALBUMS.album_created_time, ALBUMS.album_modified_time, ALBUMS.album_link, ALBUMS.album_visibility,
PHOTOS.photo_id, PHOTOS.photo_caption, PHOTOS.photo_created_time, PHOTOS.photo_modified_time, PHOTOS.photo_link
FROM ALBUMS
INNER JOIN PHOTOS ON ALBUMS.album_id = PHOTOS.album_id;

CREATE VIEW VIEW_TAG_INFORMATION AS
SELECT TAGS.tag_photo_id, TAGS.tag_subject_id, TAGS.tag_created_time, TAGS.tag_x, TAGS.tag_y
FROM TAGS;

CREATE VIEW VIEW_EVENT_INFORMATION AS
SELECT USER_EVENTS.event_id, USER_EVENTS.event_creator_id, USER_EVENTS.event_name, USER_EVENTS.event_tagline,
USER_EVENTS.event_description, USER_EVENTS.event_host, USER_EVENTS.event_type, USER_EVENTS.event_subtype, USER_EVENTS.event_address,
CITIES.city_name, CITIES.state_name, CITIES.country_name,
USER_EVENTS.event_start_time, USER_EVENTS.event_end_time
FROM USER_EVENTS
LEFT JOIN CITIES ON USER_EVENTS.event_city_id = CITIES.city_id;