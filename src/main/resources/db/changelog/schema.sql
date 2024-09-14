create table if not exists Users
(
    id uuid default gen_random_uuid(),
    email text unique not null,
    sub text not null,
    primary key (id)
);

create table if not exists Report
(
    id bigint not null,
    created_at timestamp with time zone not null,
    token_count bigint not null,
    user_id uuid not null,
    primary key (id),
    foreign key (user_id) references Users(id)
);
