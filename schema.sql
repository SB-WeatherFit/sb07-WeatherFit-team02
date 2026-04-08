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
    address_first                      varchar(255),
    address_second                     varchar(255),
    address_third                      varchar(255),

    constraint check_weather_as_word check ( as_word IN ('WEAK', 'MODERATE', 'STRONG')),
    constraint check_weather_sky_status check (sky_status IN ('CLEAR', 'MOSTLY_CLOUDY', 'CLOUDY')),
    constraint check_weather_type check (type IN ('NONE', 'RAIN', 'RAIN_SNOW', 'SNOW', 'SHOWER'))
);

create table feeds
(
    id               uuid PRIMARY KEY,
    created_at       timestamp with time zone not null DEFAULT CURRENT_TIMESTAMP,
    updated_at       timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id        uuid                     not null default '00000000-0000-0000-0000-000000000000',
    content          text                     not null,
    weather_snapshot JSONB                    not null,

    CONSTRAINT fk_feeds_users foreign key (author_id) references users (id) on delete set default
);

create table clothes
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    type       varchar(255)             not null,
    owner_id   uuid                     not null,
    image_key  TEXT,
    name       varchar(255)             not null,

    constraint check_clothes_type check (type in
                                         ('TOP', 'BOTTOM', 'DRESS', 'OUTER', 'UNDERWEAR', 'ACCESSORY', 'SHOES', 'SOCKS',
                                          'HAT', 'BAG', 'SCARF', 'ETC')),
    CONSTRAINT fk_clothes_users foreign key (owner_id) references users (id)
);

create table clothes_attribute_types
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    name       text                     not null

);

create table selectable_values
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone not null default CURRENT_TIMESTAMP,
    type_id    uuid                     not null,
    option     text                     not null,

    CONSTRAINT fk_selectable_values_clothes_attribute_types foreign key (type_id) references clothes_attribute_types (id),
    CONSTRAINT uk_type_option unique (type_id, option)

);

create table clothes_attributes
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    clothes_id uuid                     not null,
    value_id   uuid                     not null,

    CONSTRAINT fk_clothes_attributes_clothes foreign key (clothes_id) references clothes (id),
    constraint fk_clothes_attributes_selectable_values foreign key (value_id) references selectable_values (id)

);

create table comments
(

    id         uuid PRIMARY KEY,
    created_at timestamp with time zone not null DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id  uuid                     not null default '00000000-0000-0000-0000-000000000000',
    feed_id    uuid                     not null,
    content    TEXT                     not null,

    constraint fk_comments_users foreign key (author_id) references users (id) on delete set default,
    constraint fk_comments_feeds foreign key (feed_id) references feeds (id) on delete cascade
);

create table feed_likes
(
    id            uuid PRIMARY KEY,
    created_at    timestamp with time zone not null DEFAULT CURRENT_TIMESTAMP,
    updated_at    timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    feed_id       uuid                     not null,
    liked_user_id uuid                     not null,

    constraint fk_feed_likes_feeds foreign key (feed_id) references feeds (id) on delete cascade,
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
    content     text                     not null,

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
    group_id    uuid                     not null,

    CONSTRAINT fk_receiver_id FOREIGN key (receiver_id) REFERENCES users (id),
    CONSTRAINT check_notification_level CHECK (level IN ('INFO', 'WARNING', 'ERROR'))
);

CREATE INDEX idx_notifications_group_id ON notifications (group_id);

create table feed_clothes
(
    id               uuid primary key,
    created_at       timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at       timestamp with time zone not null default CURRENT_TIMESTAMP,
    feed_id          uuid                     not null,
    clothes_snapshot JSONB                    not null,

    constraint fk_feed_clothes_feeds foreign key (feed_id) references feeds (id) on delete cascade
); -- TODO GIN 인덱스 설정

create table profiles
(
    id                      uuid primary key,
    created_at              timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at              timestamp with time zone not null default CURRENT_TIMESTAMP,

    birth_date              date,
    gender                  varchar(20)              not null,
    latitude                double precision,
    longitude               double precision,
    x                       integer,
    y                       integer,
    temperature_sensitivity smallint                 not null default 3,
    user_id                 uuid                     not null unique,
    profile_image_key       text,

    constraint check_sensitivity_range check ((temperature_sensitivity between 1 and 5)),
    constraint check_gender check (gender in ('MALE', 'FEMALE', 'OTHER')),
    constraint fk_profiles_users foreign key (user_id) references users (id)
);

create table profile_location_names
(
    profile_id    uuid         not null,
    location_name varchar(255) not null,

    constraint fk_profile_location_names_profiles foreign key (profile_id) references profiles (id)
);

create table social_accounts
(
    id               uuid primary key,
    created_at       timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated_at       timestamp with time zone not null default CURRENT_TIMESTAMP,
    user_id          uuid                     not null,
    provider         varchar(20)              not null,
    provider_user_id varchar(255)             not null,
    provider_email   varchar(255)             not null,

    constraint fk_social_accounts_users foreign key (user_id) references users (id),
    constraint uk_social_accounts_provider_user_id unique (provider, provider_user_id),
    constraint uk_social_accounts_user_provider unique (user_id, provider),
    constraint check_social_provider check (provider in ('GOOGLE'))
);

-- 삭제된 유저 센티널
INSERT INTO users (id, role, email, name, password)
VALUES ('00000000-0000-0000-0000-000000000000', 'USER', 'deleted@system.local', '삭제된 유저', 'DELETED')
ON CONFLICT (id) DO NOTHING;