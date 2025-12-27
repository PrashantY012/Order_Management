-- KEYS = product keys
-- ARGV = qty1, qty2, ..., userId
local userId = ARGV[#ARGV]
local cartLockKey = "cart:lock:" .. userId

for i = 1, #KEYS do
	local qty = tonumber(ARGV[i])

	redis.call("HINCRBY", KEYS[i], "stock:reserve", -qty)
	redis.call("HINCRBY", KEYS[i], "stock:available", qty)
end

redis.call("DEL", cartLockKey)

return 1
