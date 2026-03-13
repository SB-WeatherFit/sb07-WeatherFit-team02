create table users
(
    id         uuid primary key,
    created_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    locked     boolean                  not null default false,
    role       varchar(20)              not null,
    email      varchar(255)             not null,
    name       varchar(255)             not null,
    password   varchar(255)             not null,

    constraint check_user_role check ( role in ('USER', 'ADMIN'))
);

create table weathers
(

    id                                 uuid primary key,
    created_at                         timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at                         timestamp with time zone not null default CURRENT_TIMESTAMP,
    amount                             double precision,
    as_word                            varchar(20)              NOT NULL,
    compared_to_day_before             double precision,
    current                            double precision,
    latitude                           double precision,
    longitude                          double precision,
    max                                double precision,
    min                                double precision,
    probability                        double precision,
    sky_status                         varchar(20)              NOT NULL,
    speed                              double precision,
    temperature_compared_to_day_before double precision,
    temperature_current                double precision,
    type                               varchar(20)              NOT NULL,
    forecast_at                        timestamp with time zone,
    forecasted_at                      timestamp with time zone,
    address                            varchar(255),

    constraint check_weather_as_word check ( as_word IN ('WEAK', 'MODERATE', 'STRONG')),
    constraint check_weather_sky_status check (sky_status IN ('CLEAR', 'MOSTLY_CLOUDY', 'CLOUDY')),
    constraint check_weather_type check (type IN ('NONE', 'RAIN', 'RAIN_SNOW', 'SNOW', 'SHOWER'))
);

create table feeds
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id  uuid                     not null,
    content    text                     not null,
    weather_id uuid                     not null,

    CONSTRAINT fk_feeds_users foreign key (author_id) references users (id),
    constraint fk_feeds_weathers foreign key (weather_id) references weathers (id)
);
create table clothes
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    type       varchar(255)             not null,
    owner_id   uuid                     not null,
    image_url  TEXT,
    name       varchar(255)             not null,


    constraint check_clothes_type check (type in
                                         ('TOP', 'BOTTOM', 'DRESS', 'OUTER', 'UNDERWEAR', 'ACCESSORY', 'SHOES', 'SOCKS',
                                          'HAT', 'BAG', 'SCARF', 'ETC')),
    CONSTRAINT fk_clothes_users foreign key (owner_id) references users (id)
);

create table clothing_attribute_types
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    name       text                     not null

)

create table selectable_values
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    type_id    uuid                     not null,
    value      text                     not null,

    CONSTRAINT fk_selectable_values_clothing_attribute_types foreign key (type_id) references clothing_attribute_types (id)

)

create table clothing_attributes
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cloth_id   uuid                     not null,
    type_id    uuid                     not null,
    value      text                     not null,

    CONSTRAINT fk_clothing_attributes_clothes foreign key (cloth_id) references clothes (id),
    CONSTRAINT fk_clothing_attributes_clothing_attribute_types foreign key (type_id) references clothing_attribute_types (id),
    CONSTRAINT uk_clothes_types unique (cloth_id, type_id)

);

create table comments
(

    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id  uuid,
    feed_id    uuid,
    content    TEXT,

    constraint fk_comments_users foreign key (author_id) references users (id),
    constraint fk_comments_feeds foreign key (feed_id) references feeds (id)
);



create table feed_likes
(
    id            uuid PRIMARY KEY,
    created_at    timestamp with time zone not null DEFAULT CURRENT_TIMESTAMP,
    updated_at    timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    feed_id       uuid                     not null,
    liked_user_id uuid                     not null,

    constraint fk_feed_likes_feeds foreign key (feed_id) references feeds (id),
    CONSTRAINT fk_feed_likes_users foreign key (liked_user_id) references users (id)
);


create table follows
(
    id          uuid primary key,
    created_at  timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at  timestamp with time zone not null default CURRENT_TIMESTAMP,
    followee_id uuid                     not null,
    follower_id uuid                     not null,

    constraint fk_follows_followee_users foreign key (followee_id) references users (id),
    constraint fk_follows_follower_users foreign key (follower_id) references users (id),
    CONSTRAINT uk_follows_followee_follower UNIQUE (followee_id, follower_id),
    CONSTRAINT ck_no_self_follow CHECK (followee_id <> follower_id)

);

create table messages
(
    id          uuid primary key,
    created_at  timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at  timestamp with time zone not null default CURRENT_TIMESTAMP,
    receiver_id uuid                     not null,
    sender_id   uuid                     not null,
    content     text,

    CONSTRAINT fk_messages_receiver_users foreign key (receiver_id) references users (id),
    CONSTRAINT fk_messages_sender_users foreign key (sender_id) references users (id)
);

create table notifications
(
    id          uuid primary key,
    created_at  timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at  timestamp with time zone not null default CURRENT_TIMESTAMP,
    level       varchar(255)             NOT NULL,
    receiver_id uuid                     not null,
    content     varchar(1000)            not null,
    title       varchar(255)             not null,

    CONSTRAINT fk_receiver_id FOREIGN key (receiver_id) REFERENCES users (id),
    CONSTRAINT check_notification_level CHECK (level IN ('INFO', 'WARNING', 'ERROR'))
);

create table ootds
(
    id         uuid primary key,
    created_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    feed_id    uuid                     not null,
    name       varchar(255)             not null,
    image_url  TEXT,

    constraint fk_ootds_feeds foreign key (feed_id) references feeds (id) on DELETE cascade

);

create table profiles
(
    id                      uuid primary key,
    created_at              timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at              timestamp with time zone not null default CURRENT_TIMESTAMP,

    birth_date              date,
    gender                  varchar(20)              not null,
    latitude                double precision,
    longitude               double precision,
    temperature_sensitivity smallint                 not null default 3,
    user_id                 uuid                     not null unique,
    address                 text,
    profile_image_url       text,

    constraint check_sensitivity_range check ((temperature_sensitivity between 1 and 5))

);



