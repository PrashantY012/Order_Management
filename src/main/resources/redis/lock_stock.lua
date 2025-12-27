-- KEYS = product keys (product:P1, product:P2, ...)
-- ARGV = quantities + orderId + ttl

local orderId = ARGV[#ARGV - 1]
local ttl = tonumber(ARGV[#ARGV])

-- 1. check stock
for i = 1, #KEYS do
	local stock = tonumber(redis.call("HGET", KEYS[i], "stock:"))
	local qty = tonumber(ARGV[i])

	if not stock or stock < qty then
		return -1
	end
end

-- 2. lock stock
for i = 1, #KEYS do
	local qty = tonumber(ARGV[i])

	redis.call("HINCRBY", KEYS[i], "stock:", -qty)

	local productId = string.sub(KEYS[i], string.len("product:") + 1)
	local lockKey = "lock:" .. orderId .. ":" .. productId

	redis.call("SET", lockKey, qty, "EX", ttl)
end

redis.call("DEL", cartLockKey)


return 1
