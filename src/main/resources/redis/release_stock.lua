-- KEYS = product keys
-- ARGV = qty1, qty2, ...

for i = 1, #KEYS do
	local qty = tonumber(ARGV[i])

	redis.call("HINCRBY", KEYS[i], "stock:reserve", -qty)
	redis.call("HINCRBY", KEYS[i], "stock:available", qty)
end

return 1
