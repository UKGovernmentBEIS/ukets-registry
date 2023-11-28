/**
 * Produces a random number.
 * (param) low The minimum number.
 * (param) high The maximum number.
 * (returns) a number.
 */
create or replace function random_number(low int, high int)
    returns int as
$$
begin
return floor(random() * (high - low + 1) + low);
end;
$$ language plpgsql;

/**
 * Produces a random string with the provided length.
 * (param) the length.
 * (returns) a string.
 */
create or replace function random_string(integer)
    returns text
    language sql
as $$
select upper(
               substring(
                       (select string_agg(md5(random()::text), '')
                        from generate_series(
                                1,
                                ceil($1 / 32.)::integer)
                       ), 1, $1) );
$$;
