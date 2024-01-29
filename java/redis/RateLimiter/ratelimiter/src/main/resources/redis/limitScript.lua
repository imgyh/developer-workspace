local flag = 1
for i = 1, #KEYS do
    local window_start = tonumber(ARGV[1])- tonumber(ARGV[(i-1)*3+2])
    redis.call('ZREMRANGEBYSCORE', KEYS[i], '-inf', window_start)
    local current_requests = redis.call('ZCARD', KEYS[i])
    if current_requests < tonumber(ARGV[(i-1)*3+3]) then
        redis.call('ZADD', KEYS[i], tonumber(ARGV[1]), ARGV[(i-1)*3+4])
    else
        flag = 0
    end
end
return flag