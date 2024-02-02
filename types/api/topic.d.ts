export type TopicMessageResponse = {
    chunk_info: {
        initial_transaction_id: {
            account_id: string;
            nonce: number;
            scheduled: boolean;
            transaction_valid_start: string;
        };
        number: number;
        total: number;
    };
    consensus_timestamp: string;
    message: string;
    payer_account_id: string;
    running_hash: string;
    running_hash_version: number;
    sequence_number: number;
    topic_id: string;
};

export interface TopicMessagesResponse {
    messages: TopicResponse[];
    links: {
        next: string | null;
    };
}