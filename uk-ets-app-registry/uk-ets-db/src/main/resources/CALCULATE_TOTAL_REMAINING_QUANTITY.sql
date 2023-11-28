create or replace function get_remaining_quantity(arg allocation_entry)
returns bigint
as
$body$
declare
remaining_quantity integer;
begin
select coalesce(arg.entitlement, 0) - (coalesce(arg.allocated, 0) - coalesce(arg.returned, 0) - coalesce(arg.reversed, 0))
into remaining_quantity;
return remaining_quantity;
end;
$body$
language plpgsql;