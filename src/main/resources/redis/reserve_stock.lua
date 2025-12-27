-- KEYS = product keys (product:P1, product:P2, ...)
-- ARGV = qty1, qty2, ..., orderId, ttl

local orderId = ARGV[#ARGV - 1]
local ttl = tonumber(ARGV[#ARGV])
local userId = ARGV[#ARGV - 2]


--1. check cart lock
local cartLockKey = "cart:lock:" .. ARGV[#ARGV - 2] -- assume ARGV[#ARGV-2] = userId
local existingLock = redis.call("GET", cartLockKey)
if existingLock then
	return -2  -- cart is already locked
end

-- 2. check available stock
for i = 1, #KEYS do
	local available = tonumber(redis.call("HGET", KEYS[i], "stock:available"))
	local qty = tonumber(ARGV[i])

	if not available or available < qty then
		return -1
	end
end

--3. set cart lock
redis.call("SET", cartLockKey, orderId, "EX", ttl)

-- 4. reserve stock
for i = 1, #KEYS do
	local qty = tonumber(ARGV[i])

	redis.call("HINCRBY", KEYS[i], "stock:available", -qty)
	redis.call("HINCRBY", KEYS[i], "stock:reserve", qty)

	local productId = string.sub(KEYS[i], string.len("product:") + 1)
	local lockKey = "lock:" .. orderId .. ":" .. productId

	redis.call("SET", lockKey, qty, "EX", ttl)
end

return 1
