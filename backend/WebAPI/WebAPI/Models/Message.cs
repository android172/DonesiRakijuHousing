using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WebAPI.Models
{
    public class Message
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public uint Id { get; set; }
        public uint SenderId { get; set; }
        public uint ReceiverId { get; set; }
        [MaxLength(280)]
        public string Content { get; set; }
        public DateTime SendDate { get; set; }
        public bool Seen { get; set; }
    }
}
